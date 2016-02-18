%   This is an order of magnitude check of the thrust requirements
%   calculated by the four thrust controler (FTC) applied to the scernario
%   of funcube and hincube in the proceeding 4 weeks after their lauch. One
%   of them is the deputy and the other the chief.

mu = 3.986004415000000e+14;

%delta = deputyOE - chiefOE ?
%delta = funcubeOE - hincubeOE on 4/12/2013
d_a = -106.463; %m
d_e = -0.000113;
d_i = 0.001*pi/180; %rad
d_AoP = -0.483*pi/180; %rad
d_RAAN = 0*pi/180; %rad
d_M = 0.781*pi/180; %rad

%OE of deputy (funcube)
a = 7016995.871;
e = 0.006422;
i = 97.859*pi/180; %rad
AoP = 145.12*pi/180; %rad
RAAN = 50.485*pi/180; %rad
M = 67.959*pi/180; %rad

eta = sqrt(1-e^2);
n = sqrt(mu/a^3); %mean motion of deputy



%apogee thrust
 delta_v_x = (n*a/4)*(((1-e)^2/eta)*(d_AoP + d_RAAN*cos(i))+d_M); %(17)
%along track impulse delta_v_AT_a
delta_v_y = (n*a*eta/4)*(d_a/a-d_e/(1-e)); %(25)
