import { NextResponse } from "next/server"
import type { NextRequest } from "next/server"

export function middleware(req: NextRequest) {
    const token = req.cookies.get("auth_token")?.value
    const { pathname } = req.nextUrl

    // 🔐 Solo entra acá si el matcher hace que pase por /app/(protected)
    if (!token) {
        const loginUrl = new URL("/login", req.url)
        loginUrl.searchParams.set("from", pathname)
        return NextResponse.redirect(loginUrl)
    }

    return NextResponse.next()
}

// 🧠 Solo ejecutar el middleware en rutas dentro de (protected)
export const config = {
    matcher: ["/(protected)/(.*)"],
}
