CREATE TABLE IF NOT EXISTS brands (
  id UUID PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
  id UUID PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
  id UUID PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  price_amount NUMERIC NOT NULL,
  price_currency TEXT NOT NULL,
  brand_id UUID NOT NULL REFERENCES brands(id),
  category_id UUID NOT NULL REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_items_brand_id ON items (brand_id);
CREATE INDEX IF NOT EXISTS idx_items_category_id ON items (category_id);
