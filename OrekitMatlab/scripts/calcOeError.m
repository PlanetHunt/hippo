function [ controllerErrorInput, error] = calcOeError( deputy, chief )
%CALCOEERROR Summary of this function goes here
%   both inputs are 7x1 vectors of mean OEs
global desiredOffset; %use this if we arent targeting the chief exactly, but rather an offset

%use this error for plotting and evalutation
error = deputy-(chief+desiredOffset);
error(4)=wrapTo2Pi(error(4)); %AoP
error(5)=wrapTo2Pi(error(5)); %RAAN
error(6)=wrapTo2Pi(error(6)); %TA
error(7)=wrapTo2Pi(error(7)); %MA

%use use this error for the controller, as its the shortest way around the circle
controllerErrorInput = deputy-(chief+desiredOffset);
controllerErrorInput(4)=wrapToPi(controllerErrorInput(4)); %AoP
controllerErrorInput(5)=wrapToPi(controllerErrorInput(5)); %RAAN
controllerErrorInput(6)=wrapToPi(controllerErrorInput(6)); %TA
controllerErrorInput(7)=wrapToPi(controllerErrorInput(7)); %MA


end

