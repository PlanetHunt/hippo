function [R] = LVLH2ECICharles(Reci, Veci)
%Reci and Veci are column vectors in the ECI frame
%R is the rotation matrix from LVLH to ECI frames
%the LVLH frame is defined the same as in OREKIT
% see orbital mechanics for engineerring students example 7.1 pg 317
h = cross(Reci, Veci);                 % Angular momentum of A
% Unit vectors i, j,k of the co-moving frame
i = Reci/norm(Reci);  k = h/norm(h); j = cross(k,i);

R = [i, j, k];

end