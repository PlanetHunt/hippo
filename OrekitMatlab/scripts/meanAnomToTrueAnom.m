function [ f ] = meanAnomToTrueAnom(e, M )

E=M + e*sin(M);%first guess at Eccentric anomaly
for k = 1:10;%iteratively solve for E
    E =  M+e*sin(E);
end
f = 2*atan2(sqrt(1+e)*sin(E/2),sqrt(1-e)*cos(E/2))
f = wrapTo2Pi(f);
end

