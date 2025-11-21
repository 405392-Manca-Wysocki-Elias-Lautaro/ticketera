import { NextResponse } from "next/server"
import type { NextRequest } from "next/server"

// 👮‍♂️ Rutas protegidas que requieren autenticación
const protectedRoutes = [
    "/admin",
    "/staff",
    "/my-tickets",
    "/profile",
]

// 👨‍👩‍👧‍👦 Rutas públicas que un usuario autenticado no debería visitar (ej: login)
const publicOnlyRoutes = ["/login", "/signup", "/verify-email", "/forgot-password"]

export function middleware(req: NextRequest) {
    const refreshToken = req.cookies.get("refreshToken")?.value
    const sessionFlag = req.cookies.get("sessionFlag")?.value
    const { pathname } = req.nextUrl

    const isProtected = protectedRoutes.some(route => pathname.startsWith(route))
    const isPublicOnly = publicOnlyRoutes.includes(pathname)

    const isAuthenticated = refreshToken && sessionFlag

    // No autenticado → ruta protegida
    if (!isAuthenticated && isProtected) {
        const loginUrl = new URL("/login", req.url)
        loginUrl.searchParams.set("from", pathname)
        return NextResponse.redirect(loginUrl)
    }

    // Autenticado → no dejar entrar a login/register
    if (isAuthenticated && isPublicOnly) {
        return NextResponse.redirect(new URL("/dashboard", req.url))
    }

    return NextResponse.next()
}

export const config = {
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico (favicon file)
         * - manifest.json, logo.png, etc. (otros assets públicos)
         */
        "/((?!api|_next/static|_next/image|favicon.ico|manifest.json|logo.png|placeholder.svg).*)",
    ],
}