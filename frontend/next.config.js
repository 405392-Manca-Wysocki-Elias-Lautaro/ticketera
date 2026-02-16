/** @type {import('next').NextConfig} */
// import withPWA from "next-pwa";

// const withPWAConfig = withPWA({
//     dest: "public",
//     disable: process.env.NODE_ENV === "development",
// });

const nextConfig = {
    
    reactStrictMode: true,

    // Suprimir warnings de React 19 relacionados con element.ref
    webpack: (config, { isServer }) => {
        if (!isServer) {
            config.resolve.alias = {
                ...config.resolve.alias,
            };
        }
        return config;
    },

    // Suprimir warnings específicos en el entorno de desarrollo
    onDemandEntries: {
        maxInactiveAge: 25 * 1000,
        pagesBufferLength: 2,
    },

    images: {
        domains: ["cdn.ticketera.ar", "localhost"],
    },

    eslint: {
        ignoreDuringBuilds: true,
    },

    typescript: {
        ignoreBuildErrors: true,
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

export default nextConfig;
