/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.engine.commands;

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.galenWrapper.SpecValidation.SpecReader;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.galenframework.specs.SpecText;
import com.galenframework.specs.SpecText.Type;

/**
 *
 * 
 */
public class VerifyPageHeader extends General {

    public VerifyPageHeader(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Assert if [<Object>]'s Text Equals [<Data>]",
    		input = InputType.YES)
    public void verifyPageHeader() {
    	if(Element.getText().equalsIgnoreCase(Data))
    	{
    		Report.updateTestLog(Action, "HeaderText  is matched between Expected : " + Element.getText() + "Actual : " + Data, Status.DONE);
    	} else 
    	{
    		Report.updateTestLog(Action, "HeaderText  is not matched between Expected : " + Element.getText() + "Actual : " + Data, Status.FAIL);
    	}
    }

    
}
