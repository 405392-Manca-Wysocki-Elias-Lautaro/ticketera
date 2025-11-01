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
const publicOnlyRoutes = ["/login", "/register"]

export function middleware(req: NextRequest) {
    const token = req.cookies.get("refreshToken")?.value

    const { pathname } = req.nextUrl
    console.log(`🧭 Ruta: ${pathname} | Cookie: ${token ? "✅ presente" : "❌ ausente"}`);

    const isProtectedRoute = protectedRoutes.some((route) => pathname.startsWith(route))

    // 1. 🛡️ Si el usuario no está autenticado e intenta acceder a una ruta protegida
    if (!token && isProtectedRoute) {
        const loginUrl = new URL("/login", req.url)
        loginUrl.searchParams.set("from", pathname) // Guardar la página de origen
        return NextResponse.redirect(loginUrl)
    }

    // 2. 🔄 Si el usuario está autenticado e intenta acceder a login/register
    if (token && publicOnlyRoutes.includes(pathname)) {
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