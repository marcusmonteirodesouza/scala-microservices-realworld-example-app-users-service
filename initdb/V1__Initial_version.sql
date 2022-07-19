CREATE TABLE IF NOT EXISTS users(
  id UUID PRIMARY KEY,
  username text NOT NULL UNIQUE,
  email text NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  bio TEXT,
  image text,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);