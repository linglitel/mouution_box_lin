package xyz.srcbox.lin.util

class Member {
    enum class UserType {
        ALWAYS,
        ORDINARY,
        TIME_LIMIT,
        NO_LOG,
        NOTHING_CONNECT,
        ERROR
    }

    data class MemberData(var dateTime: String)

    companion object {
        const val TYPE_NO_LOGIN = -2
        suspend fun isVip(memberData: MemberData): UserType {
            return UserType.ALWAYS
        }

        fun getVipDate(onSuccess: (s: String) -> Unit) {
            onSuccess("0")
        }
    }
}


/*if (minTime != null) {
                            val ti = minTime + totalTime * 1000
                            if (ti < System.currentTimeMillis()) {
                                onSuccess("0")
                                return
                            }
                        }*/
/*if (minTime != null) {
                            val ti = minTime + totalTime * 1000
                            if (ti < System.currentTimeMillis()) {
                                return@withContext UserType.ORDINARY
                            } else {
                                return@withContext UserType.TIME_LIMIT
//                                EggUtil.tome2Date((ti))
                            }
                        }*/