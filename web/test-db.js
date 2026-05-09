import { createClient } from '@supabase/supabase-js';

const supabaseUrl = 'https://wqisqoevshaohpclduxn.supabase.co';
const supabaseKey = 'sb_publishable_WrjjMh3ADCoetq9QAbHIkA_nYV8LgS6'; // from .env
const supabase = createClient(supabaseUrl, supabaseKey);

async function test() {
  console.log('Testing Supabase connection...');
  const { data, error } = await supabase.from('appointments').select('*').limit(1);
  if (error) {
    console.error('Error connecting:', error.message);
  } else {
    console.log('Successfully connected! Data:', data);
  }
}
test();
