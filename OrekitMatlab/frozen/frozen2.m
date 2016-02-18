% frozen2.m       April 24, 2008

% orbital motion of frozen orbits

% numerical integration of "zonal"
% equations of motion with nym4

% Orbital Mechanics with Matlab

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

clear all;

global req req2 req3 nzonal jzonal

global mu j2 cja cjb cjc nycoef

% astrodynamic and utility constants

req = 6378.14;

req2 = req * req;

req3 = req * req * req;

mu = 398600.5;

dtr = pi / 180;

rtd = 180 / pi;

% initialize nym4

nycoef = 1;

% zonal gravity coefficients

jzonal(1)  =  1;
jzonal(2)  =  1.08262668355e-3;
jzonal(3)  = -2.53265648533e-6;    
jzonal(4)  = -1.61962159137e-6;    
jzonal(5)  = -2.27296082869e-7;    
jzonal(6)  =  5.40681239107e-7;    
jzonal(7)  = -3.52359908418e-7;    
jzonal(8)  = -2.04799466985e-7;    
jzonal(9)  = -1.20616967365e-7;    
jzonal(10) = -2.41145438626e-7;    
jzonal(11) =  2.44402148325e-7;    
jzonal(12) = -1.88626318279e-7;    
jzonal(13) = -2.19788001661e-7;    
jzonal(14) =  1.30744533118e-7;    
jzonal(15) = -8.23528409456e-9;    
jzonal(16) =  1.81139265112e-8;    
jzonal(17) = -1.16904733834e-7;    
jzonal(18) = -3.09424678746e-8;    

j2 = jzonal(2);

% required by j4eqm

cja = 1.5 * jzonal(2) * req * req;

cjb = 2.5 * jzonal(3) * req * req * req;

cjc = 1.875 * jzonal(4) * req * req * req * req;

% begin simulation

clc; home;
   
fprintf('\n          program frozen2\n');
   
fprintf('\n< orbital motion of frozen orbits >\n\n');

% request simulation period

while (1)
    
   fprintf('\nplease input the simulation period (days)\n');

   ndays = input('? ');
   
   if (ndays > 0)
      break;
   end 
   
end

tsim = 86400 * ndays;

% request error tolerance

while (1)
    
   fprintf('\nplease input the algorithm step size (minutes)');
   fprintf('\n(a value between 1 and 2 is recommended)\n');

   dtnym = input('? ');
   
   if (dtnym > 0)
      break;
   end 
   
end

dtnym = 60 * dtnym;

% request order of gravity model

while(1)
    
   fprintf('\nplease input the number of zonals to include\n');
   fprintf('(0 <= zonals <= 18)\n');
   
   nzonal = input('? ');
   
   if (nzonal >= 0 && nzonal <= 18)
      break;
   end
   
end   

% request graphics step size
   
while (1)
    
   fprintf('\nplease input the graphics step size (days)\n');

   dtstep = input('? ');
   
   if (dtstep > 0)
      break;
   end 
   
end

dtstep = 86400 * dtstep;

% request initial mean orbital elements

fprintf('\ninitial mean orbital elements\n');

oemean = getoe([1;1;1;1;0;0]);

% convert to osculating orbital elements

oeosc = mean2osc(oemean);

% determine initial eci state vector

[ri, vi] = orb2eci(mu, oeosc);

% begin simulation

clc; home;

fprintf('\n  working ...\n');

ti = -dtstep;

% initial graphic data points

npts = 1;

y1(npts) = oemean(2);

y2(npts) = oemean(4) * rtd;

x1(npts) = 0;

while(1)
    
   ti = ti + dtstep;
   
   tf = ti + dtstep;
   
   % integrate from ti to tf
   
   lastpass = 0;
   
   tfn = tf;
   
   tin = ti;
   
   dt = dtnym;
   
   while(1)
       
      a = tfn - tin;
      
      if (abs(a) < dt)
         dt = a * sign(a);
         lastpass = 1;
      end
      
      if (nzonal == 4)
          
         [rf, vf] = nym4('j4eqmn', 3, tin, dt, ri, vi);
         
      else
          
         [rf, vf] = nym4('frzeqm', 3, tin, dt, ri, vi);
         
      end
      
      if (lastpass == 1)
          
         break;
         
      end
      
      ri = rf;
      
      vi = vf;
      
      tin = tin + dt;
      
   end

   % create graphics data
      
   npts = npts + 1;
      
   % compute current osculating orbital elements
   
   oeosc = eci2orb1(mu, rf, vf);
   
   % convert to mean orbital elements
   
   oetmp = oeosc(6);

   a = sin(oeosc(6)) * sqrt(1 - oeosc(2) * oeosc(2));
   
   b = oeosc(2) + cos(oeosc(6));

   eanom = atan3(a, b);

   oeosc(6) = mod(eanom - oeosc(2) * sin(eanom), 2.0 * pi);
   
   % calculate mean orbital elements
   
   oemean = osc2mean(oeosc);
   
   % fill plot arrays
   
   y1(npts) = oemean(2);
   
   y2(npts) = oemean(4) * rtd;
   
   x1(npts) = tf / 86400;
      
   % check for end of simulation
   
   if (tf >= tsim)
       
      break;
      
   end 
   
end   

% create and label plots

clc; home;

while(1)
    
   fprintf('\nwould you like to plot eccentricity vs time (y = yes, n = no)\n');
   
   yn = lower(input('? ', 's'));
   
   if (yn == 'y' || yn == 'n')
      break;
   end
   
end

if (yn == 'y')
    
   plot(x1, y1, '.r');

   title('Frozen Orbit Evolution', 'FontSize', 16);
   
   xlabel('Simulation Time (days)', 'FontSize', 12);

   ylabel('Eccentricity', 'FontSize', 12);
   
   grid;
   
end

while(1)
    
   fprintf('\nwould you like to plot argument of perigee vs time (y = yes, n = no)\n');
   
   yn = lower(input('? ', 's'));
   
   if (yn == 'y' || yn == 'n')
      break;
   end
   
end

if (yn == 'y')
    
   plot(x1, y2, '.r');

   title('Frozen Orbit Evolution', 'FontSize', 16);
   
   xlabel('Simulation Time (days)', 'FontSize', 12);

   ylabel('Argument of perigee (degrees)', 'FontSize', 12);

   grid;
   
end

while(1)
    
   fprintf('\nwould you like to plot argument of perigee vs eccentricity (y = yes, n = no)\n');
   
   yn = lower(input('? ', 's'));
   
   if (yn == 'y' || yn == 'n')
      break;
   end
   
end

if (yn == 'y')
    
   plot(y2, y1, '.r');
   
   title('Frozen Orbit Evolution', 'FontSize', 16);
   
   xlabel('Argument of perigee (degrees)', 'FontSize', 12);

   ylabel('Eccentricity', 'FontSize', 12);

   grid;
   
end
  
% create eps graphics file with tiff preview

print -depsc -tiff -r300 frozen2.eps


   
