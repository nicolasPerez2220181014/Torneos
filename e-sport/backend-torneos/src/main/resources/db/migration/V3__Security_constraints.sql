-- V3__Security_constraints.sql

-- Optimistic locking - Add version columns
ALTER TABLE tournaments ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE ticket_orders ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE ticket_sale_stages ADD COLUMN version BIGINT DEFAULT 0;

-- Business constraints
ALTER TABLE ticket_sale_stages 
ADD CONSTRAINT check_capacity_positive CHECK (capacity > 0);

ALTER TABLE ticket_sale_stages 
ADD CONSTRAINT check_price_non_negative CHECK (price >= 0);

ALTER TABLE ticket_orders 
ADD CONSTRAINT check_quantity_positive CHECK (quantity > 0);

-- Performance indexes
CREATE INDEX idx_tournaments_organizer_status 
ON tournaments(organizer_id, status);

CREATE INDEX idx_ticket_orders_tournament_status 
ON ticket_orders(tournament_id, status);

-- Additional security constraints
ALTER TABLE tournaments 
ADD CONSTRAINT check_start_before_end 
CHECK (start_date_time < end_date_time);

ALTER TABLE tournament_admins 
ADD CONSTRAINT unique_tournament_admin 
UNIQUE (tournament_id, sub_admin_user_id);