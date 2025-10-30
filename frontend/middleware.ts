import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(req: NextRequest) {
    const token = req.cookies.get("auth_token")?.value;
    const { pathname } = req.nextUrl;

    // 🟢 Rutas públicas adicionales
    const publicPaths = [
        "/login",
        "/register",
        "/forgot-password",
        "/api/public",
        "/manifest.json",
        "/favicon.ico",
        "/_next",
    ];

    const isPublic = publicPaths.some((path) => pathname.startsWith(path));

    if (!token && !isPublic) {
        const loginUrl = new URL("/login", req.url);
        loginUrl.searchParams.set("from", pathname);
        return NextResponse.redirect(loginUrl);
    }

    if (token && ["/login", "/register"].some((path) => pathname.startsWith(path))) {
        return NextResponse.redirect(new URL("/dashboard", req.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: [
        "/(protected)/(.*)",

        "/((?!_next/static|_next/image|favicon.ico|api/public).*)",
    ],
};
