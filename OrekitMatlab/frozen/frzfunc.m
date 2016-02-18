function y = frzfunc(x)

% frozen orbit - perigee perturbation

% required by frozen1.m

% Orbital Mechanics with Matlab

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

global j2 j3 req sma inc argper mm

% unload current solution

ecc = x;

sp = sin(inc);

sp2 = sp * sp;

cp = cos(inc);
cp2 = cp * cp;

% semiparameter

slr = sma * (1 - ecc * ecc);

% calculate perigee perturbation

tmp1 = 1.5 * j2 * req * req * mm * (2 - 2.5 * sp2) / (slr * slr);

tmp2 = -1.5 * j3 * req * req * req * sin(argper) * mm ...
       / (slr * slr * slr * ecc * sp);

tmp3 = (1.25 * sp2 - 1) * sp2 + ecc * ecc * (1 - (35 / 4) * sp2 * cp2);
  
y = tmp1 + tmp2 * tmp3;

