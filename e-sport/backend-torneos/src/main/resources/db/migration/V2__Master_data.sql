-- V2__Master_data.sql

-- Insert default categories
INSERT INTO categories (name, active) VALUES 
('Deportes Electrónicos', true),
('Battle Royale', true),
('MOBA', true),
('FPS', true),
('Estrategia', true);

-- Insert default game types
INSERT INTO game_types (name, active) VALUES 
('League of Legends', true),
('Fortnite', true),
('Counter-Strike 2', true),
('Valorant', true),
('Dota 2', true),
('Apex Legends', true);

-- Insert test users
INSERT INTO users (email, full_name, role) VALUES 
('admin@torneos.com', 'Admin Torneos', 'ORGANIZER'),
('user1@test.com', 'Usuario Test 1', 'USER'),
('user2@test.com', 'Usuario Test 2', 'USER'),
('organizer@test.com', 'Organizador Test', 'ORGANIZER');