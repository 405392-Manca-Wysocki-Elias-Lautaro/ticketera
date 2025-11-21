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

    devIndicators: {
        buildActivityPosition: 'bottom-right'
    },

    // 👇 entero para reescrituras / proxies
    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: "http://localhost:8080/api/:path*",
            },
        ];
    },

};

export default nextConfig;
