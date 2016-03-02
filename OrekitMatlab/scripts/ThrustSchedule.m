classdef ThrustSchedule
    %SCHEDULE the class that contains the schedule events.
    
    properties
        type %type of the thrust, could be 1, 2, 3 or 4.
        thrustWindowStart % When thrusting window starts an array
        thrustWindowEnd % When the thrusting window ends an array
        started % 1 or 0 toggle which is switched bz oreki.
    end
    
    methods
        
        %% Constructor function for the thrustschedule.
        % this function create new ThrustSChedule objects.
        function obj = ThrustSchedule(type, thrustWindowStart, thrustWindowEnd, started)
            if(nargin>0)
                obj.started = started;
                obj.type = type;
                obj.thrustWindowStart = thrustWindowStart;
                obj.thrustWindowEnd = thrustWindowEnd;
            end
        end
        
        %% Returns if the event overlaps with another one.
        % It compares the start and end of two events and finds out if they
        % have any overlaps.
        % returns 1 if true, 0 if false.
        function value = overlaps(obj, obj2)
           value = 0;
           startDateNum = datenum(obj2.thrustWindowStart);
           endDateNum = datenum(obj2.thrustWindowEnd);
           if(datenum(obj.thrustWindowStart)>startDateNum && datenum(obj.thrustWindowStart)<endDateNum)
               value = 1;
           end
           if(datenum(obj.thrustWindowEnd)>startDateNum && datenum(obj.thrustWindowEnd)<endDateNum)
               value = 1;
           end
        end
    end
    
end

