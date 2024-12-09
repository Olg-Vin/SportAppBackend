package org.vinio.entities.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Список доступных в системе ролей + их соотношение с числом
 * */
@RequiredArgsConstructor
@Getter
public enum Role {
    ADMIN, USER, MODERATOR


}