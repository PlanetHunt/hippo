function [ error ] = calcOeError( deputy, chief )
%CALCOEERROR Summary of this function goes here
%   both inputs are 7x1 vectors of mean OEs

error = deputy-chief;

error(4)=wrapToPi(error(4)); %AoP
error(5)=wrapToPi(error(5)); %RAAN
error(6)=wrapToPi(error(6)); %TA
error(6)=wrapToPi(error(7)); %MA


end

