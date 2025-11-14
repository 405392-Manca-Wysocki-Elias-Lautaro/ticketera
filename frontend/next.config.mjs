/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    domains: ["cdn.ticketera.ar", "localhost"], // permití URLs externas
  },
  eslint: {
    // ✅ No detiene la build si hay errores de Lint
    ignoreDuringBuilds: true,
  },
  typescript: {
    // ✅ No detiene la build si hay errores de TypeScript
    ignoreBuildErrors: true,
  },
  env: {
    // Mercado Pago Public Key - usar variable de entorno o fallback para desarrollo
    MERCADOPAGO_PUBLIC_KEY: process.env.MERCADOPAGO_PUBLIC_KEY || 'APP_USR-5dab3fb3-e699-43b4-996f-a65f976a354a',
  },
};

export default nextConfig;
