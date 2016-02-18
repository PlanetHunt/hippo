function ydot = j4eqmn(t, r, v) 

% second order equations of orbital motion

% includes j2, j3, j4

% input

%  t = simulation time (seconds)
%  r = position vector
%  v = velocity vector

% output

%  ydot = integration vector

% Orbital Mechanics with Matlab

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

global mu cja cjb cjc

r2 = r(1) * r(1) + r(2) * r(2) + r(3) * r(3);

rmag = sqrt(r2);

w = r(3) / rmag;
w2 = w * w;

c1 = mu / (rmag * r2);
c2 = cja / r2;
d3 = cjb / (r2 * rmag);
r4 = r2 * r2;
d4 = cjc / r4;

c4 = d4 * (1 - 14 * w2 + 21 * w2 * w2);
c41 = d4 * (5 - 23.3333 * w2 + 21 * w2 * w2);
c3 = (1 + c2 * (1 - 5 * w2) + d3 * (3 - 7 * w2) * w - c4) * c1;

ydot(1) = -r(1) * c3;
ydot(2) = -r(2) * c3;
ydot(3) = -c1 * r(3) * (1 + c2 * (3 - 5 * w2) + d3 * (6 - 7 * w2) ...
           * w - c41) + c1 * rmag * d3 * 3/5;


 

