import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'io.ionic.starter',
  appName: 'zalo-frontend',
  webDir: 'dist',
  server: {
    url: 'http://10.0.2.2:8100',
    cleartext: true,
  },
};

export default config;
