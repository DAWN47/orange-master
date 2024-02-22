package com.shuai.service;

import com.shuai.util.Result;

public interface RoleService {


    Result banRoleById(Long id);

    Result unlockRoleById(Long id);
}
