import pg from 'pg';

const { Client } = pg;
const password = 'Barangay@1403202003';
const user = 'postgres.wqisqoevshaohpclduxn';

const regions = [
  'ap-northeast-1', 'ap-southeast-1', 'ap-southeast-2', 'ap-south-1',
  'us-east-1', 'us-west-1', 'us-west-2',
  'eu-west-1', 'eu-west-2', 'eu-west-3', 'eu-central-1'
];
const prefixes = ['aws-0', 'aws-1'];
const ports = [6543];

async function checkPooler(host, port) {
  return new Promise((resolve) => {
    const client = new Client({
      user, password, host, port, database: 'postgres',
      ssl: { rejectUnauthorized: false },
      connectionTimeoutMillis: 3000
    });
    
    client.connect()
      .then(async () => {
        await client.end();
        resolve({ host, port, success: true });
      })
      .catch(err => {
        resolve({ host, port, success: false, error: err.message });
      });
  });
}

async function scan() {
  console.log('Scanning all possible Supabase pooler domains...');
  const promises = [];
  
  for (const prefix of prefixes) {
    for (const region of regions) {
      for (const port of ports) {
        const host = `${prefix}-${region}.pooler.supabase.com`;
        promises.push(checkPooler(host, port));
      }
    }
  }

  const results = await Promise.all(promises);
  const successes = results.filter(r => r.success);
  
  if (successes.length > 0) {
    console.log('\nSUCCESS! Found the correct pooler:');
    console.log(successes);
  } else {
    console.log('\nALL FAILED.');
    const timeouts = results.filter(r => r.error.includes('timeout'));
    const authErrs = results.filter(r => r.error.includes('password'));
    if (authErrs.length > 0) {
      console.log('Found instances that responded but rejected auth:', authErrs);
    }
  }
}

scan();
