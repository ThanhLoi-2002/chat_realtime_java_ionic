import { toast } from "./toast"

export async function tryCatch(func: () => Promise<any>, error?: () => void) {
    try {
        return await func()
    } catch (e: any) {
        // console.log(e)
        toast({
            color: 'danger',
            message: e.message
        })
        error && error()
        return null
    }
}