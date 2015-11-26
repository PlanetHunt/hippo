function [start_time, duration] = calc_apogee_command(t_h, m_a_h, p_apo_comm)
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
% Variables:
% t_h: Time history of last values (double array with same length as m_a_h)
% m_a_h: Mean anomaly history of last values (double array with same length as t_h)
% p_apo_comm: Percentage of half keplerian period used for apogee command

% Check whether both inputs have the same length:
if (length(t_h)~=length(m_a_h))
    error('Error in apogee command calculation -> Inputs do not have the same size!');
end

% Filter previous apogee passage out of data:
d_m_a = diff(m_a_h);
filt_apogee = find(d_m_a<-pi,1,'last');
t_h = t_h(filt_apogee+1:end);
m_a_h = m_a_h(filt_apogee+1:end);

% Calculate time of next apogee passage:
coeff_m_a = polyfit(t_h, m_a_h,1);
t_vec = [t_h(1):datenum([0,0,0,0,0,1]):t_h(1)+datenum([0,0,0,1,45,0])];
m_a_vec = polyval(coeff_m_a,t_vec);
apo = m_a_vec(m_a_vec<pi);
apo_time = t_h(1)+datenum([0,0,0,0,0,1])*length(apo);

% Calculate half keplerian period:
peri = m_a_vec(m_a_vec<0);
peri_time = t_h(1)+datenum([0,0,0,0,0,1])*length(peri);
kepl_per = apo_time-peri_time;

% Calculate start_time and duration:
start_time = apo_time - kepl_per*p_apo_comm;
duration = kepl_per*p_apo_comm*2;
end

