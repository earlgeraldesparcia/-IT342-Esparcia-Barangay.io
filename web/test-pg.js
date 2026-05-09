import pg from 'pg';

const { Client } = pg;

const client = new Client({
  user: 'postgres.wqisqoevshaohpclduxn',
  password: 'Barangay%401403202003',
  host: 'aws-1-ap-northeast-1.pooler.supabase.com',
  port: 6543,
  database: 'postgres',
  ssl: {
    rejectUnauthorized: false
  },
  connectionTimeoutMillis: 10000
});

async function test() {
  console.log('Connecting via PG protocol...');
  try {
    await client.connect();
    console.log('Connected successfully!');
    const res = await client.query('SELECT 1 as result');
    console.log('Query result:', res.rows[0]);
  } catch (err) {
    console.error('Connection error:', err.message);
  } finally {
    await client.end();
  }
}

test();
