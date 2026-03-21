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

    return {
        timeAgo, formatTime
    }
}