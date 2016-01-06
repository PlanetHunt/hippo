function [ ] = plotOrbitElements( timeVector, oeVector )
%PLOTORBITELEMENTS plots the 6 orbital elements agains time
%   timeVector = [t1 t2 ...tn]
%   oeVector = [a1 a2 ...an;
%               e1 e2 ...en;
%                i1 i2 ...in;
%                RAAN1 RAAN2 ...RAANn;
%                AoP1 AoP2 ...AoPn;
%                M1 M2 ...Mn]
%


figure; clf;
hold on;
title('semimajor axis (a) vs Time');
xlabel('time (s)'); ylabel('a (m)');
plot(timeVector,oeVector{:}(1),'--');



end

