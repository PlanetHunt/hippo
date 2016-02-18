function [rf, vf] = nym4(deq, n, tp, dt, rs, vs)

% solve a system of second order differential equations

% fourth order Nystrom method (fixed step size)

% input

%  deq = function name of systems of
%        differential equations
%  n   = number of equations in user-defined
%        system of differential equations
%  tp  = current simulation time
%  dt  = integration step size
%  rs  = position vector at initial time
%  vs  = velocity vector at initial time

% output

%  rf = position vector at time = tp + dt
%  vf = velocity vector at time = tp + dt

% Orbital Mechanics with Matlab

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

global nycoef

global a5 a6 a7 a8 a9 b5 b6 b7 b8 b9
global c5 c6 c7 c8 c9 d5 d6

if (nycoef == 1)
      
   % define integration coefficients
     
   a5 = 0.045;
   a6 = 0.3;
   a7 = 13 / 126;
   a8 = 5 / 18;
   a9 = 5 / 42;
   b5 = 7 / 600;
   b6 = 7 / 30;
   b7 = 7 / 15;
   b8 = 7 / 6;
   b9 = 25 / 63;
   c5 = 0.7;
   c6 = 19 / 78;
   c7 = 35 / 312;
   c8 = 15 / 104;
   c9 = 64 / 39;
   d5 = 70 / 39;
   d6 = 15 / 13;
     
   nycoef = 0;
end

time = tp;

for i = 1:1:n
    r(i) = rs(i);
    v(i) = vs(i);
end

av = feval(deq, time, r, v);

time = tp + a6 * dt;

for i = 1:1:n
    a(i) = dt * av(i);
    r(i) = rs(i) + dt * (a6 * vs(i) + a5 * a(i));
    v(i) = vs(i) + a6 * a(i);
end

av = feval(deq, time, r, v);

time = tp + c5 * dt;

for i = 1:1:n
    b(i) = dt * av(i);
    r(i) = rs(i) + dt * (c5 * vs(i) + b5 * a(i) + b6 * b(i));
    v(i) = vs(i) - b7 * a(i) + b8 * b(i);
end

av = feval(deq, time, r, v);

time = tp + dt;

for i = 1:1:n
    c(i) = dt * av(i);
    r(i) = rs(i) + dt * (vs(i) + c6 * a(i) + c7 * b(i) + c8 * c(i));
    v(i) = vs(i) + c9 * a(i) - d5 * b(i) + d6 * c(i);
end

av = feval(deq, time, r, v);

for i = 1:1:n
    d(i) = dt * av(i);
    rf(i) = rs(i) + dt * (vs(i) + a7 * a(i) + a8 * b(i) + a9 * c(i));
    vf(i) = vs(i) + a7 * (a(i) + d(i)) + b9 * (b(i) + c(i));
end
