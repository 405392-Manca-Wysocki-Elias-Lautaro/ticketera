/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
    domains: ["cdn.ticketera.ar", "localhost"], // permití URLs externas
  },
};

export default nextConfig;
