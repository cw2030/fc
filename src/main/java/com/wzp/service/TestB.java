package com.wzp.service;

import f.c.common.annotation.Inject;
import f.c.common.annotation.IocBean;

@IocBean
public class TestB {

    @Inject
    public TestA a;
}
