package de.jjslink.js.processor;

import de.jjslink.annotations.ClientService;
import de.jjslink.annotations.LinkedMethod;
import de.jjslink.annotations.UserId;

@ClientService
class MathClientService {

    @LinkedMethod
    Integer calculateSquare(Integer i, @UserId String userId) {
        return i * i;
    }

}
