package com.sotatek.demo.domain.enums;

public enum MemberType {
    SLIVER,
    GOLD,
    PLATINUM;
    public static MemberType getBySalary(Double salary) {
        if(salary <= 30000) {
            return SLIVER;
        }else {
            if (salary <= 50000) {
                return GOLD;
            } else
                return PLATINUM;
        }
    }
}
