-- Usuario de prueba para testing
INSERT INTO users (id, email, full_name, password_hash, role, is_active, created_at, updated_at) 
VALUES (
    '123e4567-e89b-12d3-a456-426614174000',
    'test@organizer.com',
    'Test Organizer',
    '$2a$10$dummy.hash.for.testing',
    'ORGANIZER',
    true,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;