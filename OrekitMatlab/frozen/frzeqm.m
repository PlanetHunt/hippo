function ydot = frzeqm(t, r, v)

% second order "zonal" equations of orbital motion

% required by frozen2.m

% Orbital Mechanics with Matlab

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

global mu req req2 req3 nzonal jzonal

agrav = zeros(3, 1);

rmag = sqrt(r(1)*r(1) + r(2)*r(2) + r(3)*r(3));

rmag3 = rmag * rmag * rmag;

for i = 1:1:3
    u(1, i) = -mu * r(i) / rmag3;
end

if (nzonal > 1)
    
   zr = r(3) / rmag;
   
   zr2 = zr * zr;
   
   rmag5 = rmag * rmag * rmag3;
   
   a = 1.5 * mu * req2 / rmag5;
   
   u(2, 1) = a * (5 * zr2 - 1) * r(1);
   u(2, 2) = a * (5 * zr2 - 1) * r(2);
   u(2, 3) = a * (5 * zr2 - 3) * r(3);
   
end

if (nzonal > 2)
    
   zr3 = zr * zr2;
   
   rmag6 = rmag * rmag5;
   
   a = 0.5 * mu * req3 / rmag6;
   
   u(3, 1) = a * (35 * zr3 - 15 * zr) * r(1);
   u(3, 2) = a * (35 * zr3 - 15 * zr) * r(2);
   u(3, 3) = a * ((35 * zr3 - 30 * zr) * r(3) + 3 * rmag);
   
end

if (nzonal > 3)
    
   rmag2 = rmag * rmag;
   
   for n = 4:1:nzonal
       
       a = (2 * n + 1) * req * r(3) / (n * rmag2);
       
       u(n, 1) = a * u(n - 1, 1) - (n + 1) * req2 * u(n - 2, 1) / (n * rmag2);
       u(n, 2) = a * u(n - 1, 2) - (n + 1) * req2 * u(n - 2, 2) / (n * rmag2);
       u(n, 3) = a * u(n - 1, 3) - n * req2 * u(n - 2, 3) / ((n - 1) * rmag2);
       
   end
   
end

for n = 1:1:nzonal
    
    agrav(1) = agrav(1) + jzonal(n) * u(n, 1);
    agrav(2) = agrav(2) + jzonal(n) * u(n, 2);
    agrav(3) = agrav(3) + jzonal(n) * u(n, 3);
    
end

for i = 1:1:3
    
    ydot(i) = agrav(i);
    
end

