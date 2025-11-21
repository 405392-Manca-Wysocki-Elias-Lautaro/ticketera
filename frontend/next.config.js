/** @type {import('next').NextConfig} */
import withPWA from "next-pwa";

const withPWAConfig = withPWA({
    dest: "public",
    disable: process.env.NODE_ENV === "development",
});

const nextConfig = {
    
    reactStrictMode: true,

    images: {
        domains: ["cdn.ticketera.ar", "localhost"],
    },

    eslint: {
        ignoreDuringBuilds: true,
    },

    typescript: {
        ignoreBuildErrors: true,
    },

    devtools: {
        enabled: false,
    },

    devIndicators: {
        position: "bottom-left",
    },

    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: "http://localhost:8080/api/:path*",
            },
        ];
    },
};

export default withPWAConfig(nextConfig);
