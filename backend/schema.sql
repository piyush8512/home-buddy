-- ============================================================================
-- HOME BUDDY / PANTRY & GROCERY MANAGEMENT SYSTEM
-- Production Relational Database Schema (schema.sql)
-- Standard ANSI SQL & PostgreSQL 14+ Compatible
-- ============================================================================

-- Enable UUID extension if running in PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- Core user entity with internal UUID decoupled from external Firebase UID.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Application internal ID
    firebase_uid VARCHAR(128) NOT NULL UNIQUE,     -- External Firebase Auth Identity Provider UID
    email VARCHAR(255) NOT NULL UNIQUE,            -- User email address
    display_name VARCHAR(150) NOT NULL,            -- Full user name
    avatar_url TEXT,                               -- User avatar / profile image URL
    is_active BOOLEAN NOT NULL DEFAULT TRUE,       -- Account status
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_firebase_uid ON users(firebase_uid);

-- ----------------------------------------------------------------------------
-- 2. USER DEVICES TABLE (Multi-Device FCM Token Management)
-- Supports multiple devices per user (Android phones, tablets, Web browsers).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    fcm_token TEXT NOT NULL UNIQUE,
    platform VARCHAR(30) NOT NULL CHECK (platform IN ('ANDROID', 'IOS', 'WEB')),
    device_name VARCHAR(100),                      
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_device_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_devices_user ON user_devices(user_id);


-- ----------------------------------------------------------------------------
-- 3. HOUSEHOLDS TABLE
-- Multi-tenant household workspace grouping.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS households (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,                    -- e.g. "Chen Family Kitchen"
    invite_code VARCHAR(32) UNIQUE,                -- Shareable code to join (e.g. "HB-8492")
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_household_creator FOREIGN KEY (created_by_user_id) 
        REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_households_invite_code ON households(invite_code);


-- ----------------------------------------------------------------------------
-- 4. HOUSEHOLD MEMBERS TABLE
-- Many-to-many relationship with constrained role permissions.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS household_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'MEMBER' 
        CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER', 'GUEST')),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hm_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_hm_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_household_user UNIQUE (household_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_hm_household ON household_members(household_id);
CREATE INDEX IF NOT EXISTS idx_hm_user ON household_members(user_id);


-- ----------------------------------------------------------------------------
-- 5. STORAGE ZONES TABLE
-- Storage locations with constrained zone types.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS storage_zones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,                    -- e.g. "Fridge Door", "Main Pantry"
    zone_type VARCHAR(50) NOT NULL DEFAULT 'FRIDGE'
        CHECK (zone_type IN ('FRIDGE', 'FREEZER', 'PANTRY', 'COUNTERTOP', 'SPICE_RACK', 'OTHER')),
    icon VARCHAR(50) DEFAULT 'refrigerator',
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_zone_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_storage_zones_household ON storage_zones(household_id);


-- ----------------------------------------------------------------------------
-- 6. ITEM CATEGORIES TABLE
-- Relational category definitions (eliminates duplicate category names).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID,                             -- NULL for system defaults, or household-custom
    name VARCHAR(100) NOT NULL,                    -- e.g. "Dairy & Cultured", "Produce & Greens"
    color_hex VARCHAR(10) DEFAULT '#4A7C59',
    icon VARCHAR(50) DEFAULT 'category',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_categories_household ON categories(household_id);


-- ----------------------------------------------------------------------------
-- 7. PANTRY ITEMS TABLE
-- Central inventory item table.
-- • UUID PK supports offline-first mobile generation (no separate remote_id needed).
-- • TIMESTAMPTZ replaces raw *_millis for native date arithmetic.
-- • Numeric grams for protein, carbs, and fat.
-- • Normalized category & storage zone FK references.
-- • CHECK constraints for sync_status, nutri_score, and eco_impact.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pantry_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Client or Server generated UUID
    household_id UUID NOT NULL,
    storage_zone_id UUID,                          -- Relational reference to storage_zones
    category_id UUID,                              -- Relational reference to categories
    name VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    shelf_location VARCHAR(150),                   -- e.g. "Top Door Bin • Shelf A"
    barcode VARCHAR(64),                           -- UPC / EAN barcode
    package_size VARCHAR(100),                     -- e.g. "907g tub", "1 Gallon"
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity >= 0),
    unit VARCHAR(50) DEFAULT 'unit',
    expiry_at TIMESTAMPTZ NOT NULL,                -- Expiration timestamp (PostgreSQL date queries)
    stocked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_consumed BOOLEAN NOT NULL DEFAULT FALSE,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    confidence_score INT DEFAULT 95 CHECK (confidence_score BETWEEN 0 AND 100),
    price DECIMAL(10, 2) DEFAULT 0.00 CHECK (price >= 0),
    image_url TEXT,

    -- Nutritional & Environmental Data (Numeric)
    calories INT DEFAULT 0 CHECK (calories >= 0),
    protein_grams DECIMAL(8, 2) DEFAULT 0.00 CHECK (protein_grams >= 0),
    carbs_grams DECIMAL(8, 2) DEFAULT 0.00 CHECK (carbs_grams >= 0),
    fat_grams DECIMAL(8, 2) DEFAULT 0.00 CHECK (fat_grams >= 0),
    nutri_score VARCHAR(5) DEFAULT 'A' CHECK (nutri_score IN ('A', 'B', 'C', 'D', 'E')),
    eco_impact VARCHAR(5) DEFAULT 'B' CHECK (eco_impact IN ('A', 'B', 'C', 'D', 'E')),

    -- Sync & Audit Metadata
    sync_status VARCHAR(30) NOT NULL DEFAULT 'SYNCED'
        CHECK (sync_status IN ('SYNCED', 'PENDING_UPLOAD', 'PENDING_DELETE')),
    created_by_user_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pantry_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_pantry_zone FOREIGN KEY (storage_zone_id) 
        REFERENCES storage_zones(id) ON DELETE SET NULL,
    CONSTRAINT fk_pantry_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_pantry_creator FOREIGN KEY (created_by_user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_pantry_household ON pantry_items(household_id);
CREATE INDEX IF NOT EXISTS idx_pantry_expiry_at ON pantry_items(household_id, expiry_at);
CREATE INDEX IF NOT EXISTS idx_pantry_consumed ON pantry_items(household_id, is_consumed);
CREATE INDEX IF NOT EXISTS idx_pantry_barcode ON pantry_items(barcode);


-- ----------------------------------------------------------------------------
-- 8. SHOPPING ITEMS TABLE
-- Collaborative shopping list.
-- • Removed redundant added_by_initial (derived from users.display_name).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shopping_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    category_id UUID,                              -- Optional relation to categories
    store VARCHAR(100) DEFAULT 'All Stores',       -- e.g. "Trader Joe's", "Whole Foods"
    is_auto_depleted BOOLEAN NOT NULL DEFAULT FALSE,
    depletion_percent INT CHECK (depletion_percent BETWEEN 0 AND 100),
    is_checked BOOLEAN NOT NULL DEFAULT FALSE,
    price DECIMAL(10, 2) CHECK (price >= 0),
    added_by_user_id UUID,
    image_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shopping_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_shopping_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_shopping_user FOREIGN KEY (added_by_user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_shopping_household ON shopping_items(household_id);
CREATE INDEX IF NOT EXISTS idx_shopping_checked ON shopping_items(household_id, is_checked);


-- ----------------------------------------------------------------------------
-- 9. SCAN LOGS TABLE
-- Audit log of camera OCR and barcode transactions with constrained statuses.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scan_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID NOT NULL,
    user_id UUID,
    scan_type VARCHAR(50) NOT NULL 
        CHECK (scan_type IN ('BARCODE', 'OCR_LABEL', 'RECEIPT_BATCH')),
    barcode VARCHAR(64),
    raw_ocr_text TEXT,
    recognized_name VARCHAR(255),
    confidence_score INT DEFAULT 0 CHECK (confidence_score BETWEEN 0 AND 100),
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS'
        CHECK (status IN ('SUCCESS', 'MANUAL_EDITED', 'FAILED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_scan_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_scan_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_scan_logs_household ON scan_logs(household_id);
CREATE INDEX IF NOT EXISTS idx_scan_logs_barcode ON scan_logs(barcode);


-- ----------------------------------------------------------------------------
-- 10. NOTIFICATIONS TABLE
-- Expiration warnings & reminders with explicit lifecycle status tracking.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID NOT NULL,
    user_id UUID,                                  -- Target recipient, or NULL for all household
    pantry_item_id UUID,                           -- Referenced item if expiration alert
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    alert_type VARCHAR(50) NOT NULL 
        CHECK (alert_type IN ('EXPIRATION_CRITICAL', 'EXPIRATION_SOON', 'DEPLETED_ITEM', 'RESTOCK', 'SYSTEM')),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'CANCELLED')),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    scheduled_for TIMESTAMPTZ,                     -- When alert should be triggered
    sent_at TIMESTAMPTZ,                           -- Actual dispatch timestamp
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notif_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_item FOREIGN KEY (pantry_item_id) 
        REFERENCES pantry_items(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_notif_household ON notifications(household_id);
CREATE INDEX IF NOT EXISTS idx_notif_status ON notifications(status, scheduled_for);
CREATE INDEX IF NOT EXISTS idx_notif_user_unread ON notifications(user_id, is_read);


-- ----------------------------------------------------------------------------
-- 11. ACTIVITY AUDIT LOG TABLE
-- Timeline feed with entity_type constraint for polymorphic safety.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activity_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    household_id UUID NOT NULL,
    user_id UUID,
    action VARCHAR(60) NOT NULL 
        CHECK (action IN ('ITEM_ADDED', 'ITEM_CONSUMED', 'ITEM_DISCARDED', 'LIST_PURCHASED', 'MEMBER_JOINED')),
    entity_type VARCHAR(40) NOT NULL 
        CHECK (entity_type IN ('PANTRY_ITEM', 'SHOPPING_ITEM', 'HOUSEHOLD_MEMBER', 'STORAGE_ZONE')),
    entity_id UUID NOT NULL,                       -- Referenced entity UUID
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_household FOREIGN KEY (household_id) 
        REFERENCES households(id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_activity_household ON activity_logs(household_id, created_at DESC);
