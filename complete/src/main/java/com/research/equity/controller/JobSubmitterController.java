package com.research.equity.controller;


import com.research.equity.service.ISMACalculationService;
import com.research.equity.service.impl.SMACalculationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobSubmitterController {

    @Autowired
    private ISMACalculationService ismaCalculationService;

    @RequestMapping(value = "/submitJob", method = RequestMethod.GET)
    public void runSMACalculationJob() {
        ismaCalculationService.runSMACalculationJob();
    }
}
