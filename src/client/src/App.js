// src/client/src/App.js
import AppRouter from './AppRouter';
import { MailProvider } from './contexts/MailContext';
import WaveBackground from './components/jsx/WaveBackground';

function App() {
  return (
    <MailProvider>
      <AppRouter />
      <WaveBackground />
    </MailProvider>
  );
}

export default App;
