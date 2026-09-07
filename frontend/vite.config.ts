/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// No dev-server proxy — the frontend talks to the backend directly (see API_BASE_URL in
// App/Common/constants.ts). Cookies work across the two origins because they're issued against a
// shared parent domain (updates.app-jwt.cookie-domain), not because of same-site proxying.
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    // Falls back to 5173 for local (non-docker) dev; docker-compose sets SUBSCRIPTIONS_FRONTEND_HTTP_PORT
    // (see ../docker-compose.yml) so the container's listening port always matches its published port.
    port: Number(process.env.SUBSCRIPTIONS_FRONTEND_HTTP_PORT) || 5175,
    strictPort: true,
    allowedHosts: [
        'local.subscriptions.alwaysmoveforward.com',
        'local.api.subscriptions.alwaysmoveforward.com',
        'subscriptions.alwaysmoveforward.com',
        'api.subscriptions.alwaysmoveword.com'
    ],
    hmr: {
        overlay: true,
    },
    watch: {
        usePolling: true,
    },
},
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: "./src/setupTests.ts",
  },
});
