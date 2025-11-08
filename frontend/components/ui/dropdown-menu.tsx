"use client"

import * as React from "react"
import * as DropdownMenuPrimitive from "@radix-ui/react-dropdown-menu"
import { cn } from "@/lib/utils"

// Root
const DropdownMenu = ({
    children,
    ...props
}: React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Root>) => (
    <DropdownMenuPrimitive.Root {...props}>{children}</DropdownMenuPrimitive.Root>
)
DropdownMenu.displayName = "DropdownMenu"

// Trigger (🖱️ cursor-pointer)
const DropdownMenuTrigger = React.forwardRef<
    React.ElementRef<typeof DropdownMenuPrimitive.Trigger>,
    React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Trigger>
>(({ className, children, ...props }, ref) => (
    <DropdownMenuPrimitive.Trigger
        ref={ref}
        className={cn(
            "cursor-pointer outline-none focus-visible:ring-2 focus-visible:ring-ring",
            className
        )}
        {...props}
    >
        {children}
    </DropdownMenuPrimitive.Trigger>
))
DropdownMenuTrigger.displayName = DropdownMenuPrimitive.Trigger.displayName

// Group
const DropdownMenuGroup = ({
    children,
    ...props
}: React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Group>) => (
    <DropdownMenuPrimitive.Group {...props}>{children}</DropdownMenuPrimitive.Group>
)
DropdownMenuGroup.displayName = "DropdownMenuGroup"

// Portal
const DropdownMenuPortal = ({
    children,
    ...props
}: React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Portal>) => (
    <DropdownMenuPrimitive.Portal {...props}>{children}</DropdownMenuPrimitive.Portal>
)
DropdownMenuPortal.displayName = "DropdownMenuPortal"

// Sub
const DropdownMenuSub = ({
    children,
    ...props
}: React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Sub>) => (
    <DropdownMenuPrimitive.Sub {...props}>{children}</DropdownMenuPrimitive.Sub>
)
DropdownMenuSub.displayName = "DropdownMenuSub"

// RadioGroup
const DropdownMenuRadioGroup = ({
    children,
    ...props
}: React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.RadioGroup>) => (
    <DropdownMenuPrimitive.RadioGroup {...props}>
        {children}
    </DropdownMenuPrimitive.RadioGroup>
)
DropdownMenuRadioGroup.displayName = "DropdownMenuRadioGroup"

// SubTrigger (🖱️ cursor-pointer)
const DropdownMenuSubTrigger = React.forwardRef<
    React.ElementRef<typeof DropdownMenuPrimitive.SubTrigger>,
    React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.SubTrigger> & {
        inset?: boolean
    }
>(({ className, inset, children, ...props }, ref) => (
    <DropdownMenuPrimitive.SubTrigger
        ref={ref}
        className={cn(
            "flex cursor-pointer select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors focus:bg-accent data-[state=open]:bg-accent",
            inset && "pl-8",
            className
        )}
        {...props}
    >
        {children}
    </DropdownMenuPrimitive.SubTrigger>
))
DropdownMenuSubTrigger.displayName = DropdownMenuPrimitive.SubTrigger.displayName

// Content
const DropdownMenuContent = React.forwardRef<
    React.ElementRef<typeof DropdownMenuPrimitive.Content>,
    React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Content>
>(({ className, sideOffset = 4, children, ...props }, ref) => (
    <DropdownMenuPrimitive.Portal>
        <DropdownMenuPrimitive.Content
            ref={ref}
            sideOffset={sideOffset}
            className={cn(
                "z-50 min-w-[8rem] overflow-hidden rounded-md border bg-popover p-1 text-popover-foreground shadow-md",
                className
            )}
            {...props}
        >
            {children}
        </DropdownMenuPrimitive.Content>
    </DropdownMenuPrimitive.Portal>
))
DropdownMenuContent.displayName = DropdownMenuPrimitive.Content.displayName

// Item (🖱️ cursor-pointer)
const DropdownMenuItem = React.forwardRef<
    React.ElementRef<typeof DropdownMenuPrimitive.Item>,
    React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Item> & {
        inset?: boolean
    }
>(({ className, inset, children, ...props }, ref) => (
    <DropdownMenuPrimitive.Item
        ref={ref}
        className={cn(
            "relative flex cursor-pointer select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50",
            inset && "pl-8",
            className
        )}
        {...props}
    >
        {children}
    </DropdownMenuPrimitive.Item>
))
DropdownMenuItem.displayName = DropdownMenuPrimitive.Item.displayName

// Separator
const DropdownMenuSeparator = React.forwardRef<
    React.ElementRef<typeof DropdownMenuPrimitive.Separator>,
    React.ComponentPropsWithoutRef<typeof DropdownMenuPrimitive.Separator>
>(({ className, ...props }, ref) => (
    <DropdownMenuPrimitive.Separator
        ref={ref}
        className={cn("-mx-1 my-1 h-px bg-muted", className)}
        {...props}
    />
))
DropdownMenuSeparator.displayName = DropdownMenuPrimitive.Separator.displayName

export {
    DropdownMenu,
    DropdownMenuTrigger,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuPortal,
    DropdownMenuSub,
    DropdownMenuRadioGroup,
    DropdownMenuSubTrigger,
    DropdownMenuItem,
    DropdownMenuSeparator,
}
