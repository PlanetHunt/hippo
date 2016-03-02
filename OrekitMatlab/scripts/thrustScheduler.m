function [ addThrustCommandQueFlag ] = thrustScheduler(thrustType, thrustWindowStart, thrustWindowEnd)
    %% Checks if the event is happening in the right time.
    %if not set the event to be called.
    global counter;
    global recentOrbit;
    global lastSchedule;
    global initialize;
    nextSchedule = ThrustSchedule(thrustType,thrustWindowStart, thrustWindowEnd, 0);
    %% Initilize the first instance of the schedule
    if(counter==0 && ~strcmp(recentOrbit,getOrbitType(thrustType)) &&  initialize==1)
        initialize = 0;
        recentOrbit = getOrbitType(thrustType);
        counter = counter + 1;
        nextSchedule = ThrustSchedule(thrustType,thrustWindowStart, thrustWindowEnd, 1);
    %% Check if the event of the same kind is happenning.
    elseif(counter > 0 && counter <2 )
        if(recentOrbit == getOrbitType(thrustType))
            counter = counter + 1;
            nextSchedule = ThrustSchedule(thrustType,thrustWindowStart, thrustWindowEnd, 1);
        end
    else
     %% Not the same kind event, but still a legit one, see if clashes with the event before.
     %% dummy schedule
        if(counter==0 && ~strcmp(recentOrbit,getOrbitType(thrustType)))
            nextSchedule = ThrustSchedule(thrustType, thrustWindowStart, thrustWindowEnd, 0);
        else
            counter = 0;
            recentOrbit = getOrbitType(thrustType);
            nextSchedule = ThrustSchedule(thrustType,thrustWindowStart, thrustWindowEnd, 1);
            clash = overlaps(nextSchedule, lastSchedule);
        
            if(clash==1)
                nextSchedule.started = 0;
            else
                counter = counter + 1;
            end
        end
    end
    if(nextSchedule.started ~=0)
        lastSchedule = nextSchedule;
        addThrustCommandQueFlag = 1;
    else
        addThrustCommandQueFlag = 0;
    end

end

