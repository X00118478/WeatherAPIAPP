using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Weather.Models;

namespace Weather.Controllers
{
    [RoutePrefix("weather")]
    public class WeatherController : ApiController
    {
        private static readonly List<WeatherInfo> weather = new List<WeatherInfo>
        {
            new WeatherInfo
            {
                City = "Dublin",
                Conditions = "Sunny",
                MaxTemperature = 22,
                MinTemperature = 17,
                Outlook = "Rain",
                WindDirection = "North",
                WindSpeed = 5
            },
            new WeatherInfo
            {
                City = "New York",
                Conditions = "Snow",
                MaxTemperature = 0,
                MinTemperature = 0,
                Outlook = "Snow",
                WindDirection = "East",
                WindSpeed = 20
            },
            new WeatherInfo
            {
                City = "London",
                Conditions = "Rain",
                MaxTemperature = 15,
                MinTemperature = 5,
                Outlook = "Rain",
                WindDirection = "West",
                WindSpeed = 40
            }
        };

        [Route("all")]
        [HttpGet]
        public IHttpActionResult RetrieveAllWeatherInformation()
        {
            //Returning All weather information in cirty order.
            return Ok(weather.OrderBy(w => w.City).ToList());
        }

        [Route("city/{cityName:alpha}")]
        public IHttpActionResult GetWeatherInformationForCity(string cityName)
        {
            var cityWeather = weather.FirstOrDefault(w => w.City.ToUpper() == cityName.ToUpper());
            if (cityWeather == null)
                return NotFound();
            return Ok(cityWeather);
        }
    }
}
