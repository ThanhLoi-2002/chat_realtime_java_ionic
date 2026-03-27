import { useTranslate } from "./useTranslate"

type TimeInput = Date | string | number

export function useDateTime() {
    const { t } = useTranslate()

    const timeAgo = (input: TimeInput): string => {
        const now = new Date()
        const past = new Date(input)

        if (isNaN(past.getTime())) return ''

        const diff = Math.floor((now.getTime() - past.getTime()) / 1000)

        if (diff < 10) return t("justFinished")
        if (diff < 60) return `${diff} ${t('secondsAgo')}`

        const minutes = Math.floor(diff / 60)
        if (minutes < 60) return `${minutes} ${t('minutesAgo')}`

        const hours = Math.floor(minutes / 60)
        if (hours < 24) return `${hours} ${t('hoursAgo')}`

        const days = Math.floor(hours / 24)
        if (days === 1) return t('yesterday')
        if (days < 7) return `${days} ${t('daysAgo')}`

        const weeks = Math.floor(days / 7)
        if (weeks < 4) return `${weeks} ${t('weeksAgo')}`

        const months = Math.floor(days / 30)
        if (months < 12) return `${months} ${t('monthsAgo')}`

        const years = Math.floor(days / 365)
        return `${years} ${t('yearsAgo')}`
    }

    const formatTime = (input: TimeInput) => {
        const d = new Date(input)

        if (isNaN(d.getTime())) return ''

        return d.toLocaleTimeString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    const getTime = (t: any) => new Date(t).getTime()

    const formatSeparatorTime = (time: any) => {
        const date = new Date(time)
        const now = new Date()

        const isSameDay =
            date.getDate() === now.getDate() &&
            date.getMonth() === now.getMonth() &&
            date.getFullYear() === now.getFullYear()

        const yesterday = new Date()
        yesterday.setDate(now.getDate() - 1)

        const isYesterday =
            date.getDate() === yesterday.getDate() &&
            date.getMonth() === yesterday.getMonth() &&
            date.getFullYear() === yesterday.getFullYear()

        const hh = String(date.getHours()).padStart(2, '0')
        const mm = String(date.getMinutes()).padStart(2, '0')

        if (isSameDay) return `${t("today")} ${hh}:${mm}`

        if (isYesterday) return `${t("yesterday")} ${hh}:${mm}`

        const dd = String(date.getDate()).padStart(2, '0')
        const MM = String(date.getMonth() + 1).padStart(2, '0')
        const yyyy = date.getFullYear()

        return `${dd}/${MM}/${yyyy} ${hh}:${mm}`
    }

    return {
        timeAgo, formatTime, getTime, formatSeparatorTime
    }
}