function [ value ] = getOrbitType(thrustType)
%% Returns the orbit type according to the thrust types given.
% If the thurst types are 1 and 2 the orbit is going to be AB change thrust
% in Apogee/Perigee
% If the thrust types are 3 and 4 the orbit is going to be CD change thrust in
% true latitudes.
if(thrustType == 1 || thrustType == 2)
    value = 'AB';
else
    value = 'CD';
end

