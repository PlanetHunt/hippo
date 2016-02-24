function [sat,date] = getKeplerSat(mu,n)
[a,e,in,omega,raan,mean_anomaly,date,~,~] = spacecraft_init_data(mu,n);
sat = [a,e,in,omega,raan,mean_anomaly];
end