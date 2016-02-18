d_a = -0.00192995*1000;
d_e = 0.0005767;
d_i = 0.006;
d_RAAN = 0;
d_aop = 0;
d_M = 0;

mu = 3.986004415000000e+14;
  % date: 2014.07.04 16:55:41.240
        n = 14.73345459;
        a = (mu/(n*2*pi/(24*3600))^2)^(1/3);
        e = 0.0012952;
        %e = 0.01;
        i = 98.2557/180*pi;
        AoP = 258.543/180*pi;
        RAAN = 252.93/180*pi;
        mean_anomaly = 101.433/180*pi;
        
        eta = sqrt(1-e^2);
        n = sqrt(mu/a^3); %mean motion of deputy

%apogee thrust
 delta_v_x = (n*a/4)*(((1-e)^2/eta)*(d_aop + d_RAAN*cos(i))+d_M); %(17)
%along track impulse delta_v_AT_a
delta_v_y = (n*a*eta/4)*(d_a/a-d_e/(1-e)); %(25)
