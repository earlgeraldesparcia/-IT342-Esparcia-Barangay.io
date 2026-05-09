import { useState } from 'react'
import { debugRegistration } from '../utils/debugRegistration'

export default function DebugRegistration() {
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)

  const runDiagnostic = async () => {
    setLoading(true)
    console.clear()
    const diagnosticResults = await debugRegistration.runFullDiagnostic()
    setResults(diagnosticResults)
    setLoading(false)
  }

  const checkExistingData = async () => {
    setLoading(true)
    console.clear()
    const dataResults = await debugRegistration.checkExistingData()
    setResults({ existingData: dataResults })
    setLoading(false)
  }

  return (
    <div style={{ padding: '20px', fontFamily: 'monospace' }}>
      <h1>🔍 Registration Debug Tool</h1>
      
      <div style={{ marginBottom: '20px' }}>
        <button 
          onClick={runDiagnostic}
          disabled={loading}
          style={{
            padding: '10px 20px',
            marginRight: '10px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Running...' : 'Run Full Diagnostic'}
        </button>
        
        <button 
          onClick={checkExistingData}
          disabled={loading}
          style={{
            padding: '10px 20px',
            backgroundColor: '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Checking...' : 'Check Existing Data'}
        </button>
      </div>

      {results && (
        <div style={{ marginTop: '20px' }}>
          <h2>📊 Results:</h2>
          <pre style={{ 
            backgroundColor: '#f8f9fa', 
            padding: '15px', 
            borderRadius: '4px',
            overflow: 'auto',
            maxHeight: '400px'
          }}>
            {JSON.stringify(results, null, 2)}
          </pre>
        </div>
      )}

      <div style={{ marginTop: '30px', padding: '15px', backgroundColor: '#fff3cd', borderRadius: '4px' }}>
        <h3>📝 Instructions:</h3>
        <ol>
          <li>Open browser developer tools (F12)</li>
          <li>Go to Console tab</li>
          <li>Click "Run Full Diagnostic" button</li>
          <li>Check console output for detailed logs</li>
          <li>Review results displayed below</li>
        </ol>
        
        <h3>🔧 Common Issues:</h3>
        <ul>
          <li><strong>Tables missing:</strong> Run the schema.sql in Supabase SQL Editor</li>
          <li><strong>RLS blocking:</strong> Check RLS policies allow inserts</li>
          <li><strong>Auth issues:</strong> Verify Supabase credentials in .env</li>
          <li><strong>Email confirmation:</strong> Check if users need to confirm email</li>
        </ul>
      </div>
    </div>
  )
}
