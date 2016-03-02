function [ addThrustCommandQueFlag ] = thrustScheduler(thrustType)
    %% Checks if the event is happening in the right time.
    %if not set the event to be called.
    global counter;
    global recentOrbit;
    nextSchedule = 0;
    if(counter==0 && (recentOrbit ~= getOrbitType(thrustType)))
        recentOrbit = getOrbitType(thrustType);
        counter = counter + 1;
        nextSchedule = 1;
    elseif(counter > 0 && counter <2 )
        if(recentOrbit == getOrbitType(thrustType))
            counter = counter + 1;
            nextSchedule = 1;
        end
    else
        counter = 0;
        recentOrbit = getOrbitType(thrustType);
        nextSchedule = 1;
    end
    if(nextSchedule~=0)
        addThrustCommandQueFlag = 1;
    else
        addThrustCommandQueFlag = 0;
    end

end

