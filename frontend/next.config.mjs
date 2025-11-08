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
};

export default nextConfig;
