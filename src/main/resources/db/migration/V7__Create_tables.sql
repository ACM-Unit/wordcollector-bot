ALTER TABLE users ADD CONSTRAINT unique_user_name UNIQUE (name);
INSERT INTO users (name, house_points)
VALUES
    ('Dima', 49),
    ('Mama', 15),
    ('Dad', 5)
ON CONFLICT (name) DO NOTHING;