function [ M ] = trueAnomToMeanAnom(e, f )
%TRUEANOMTOMEANANOM Summary of this function goes here
%   Detailed explanation goes here

E = atan2(sqrt((1-e^2))*sin(f),e+cos(f));
E = wrapTo2Pi(E);
M = E - e*sin(E);
M = wrapTo2Pi(M);
end

